#!/usr/bin/env python3

import argparse
import re
from collections import defaultdict
from pathlib import Path


# پوشه‌هایی که نباید اسکن شوند
IGNORED_DIRECTORIES = {
    ".git",
    ".idea",
    ".gradle",
    "target",
    "build",
    "out",
    "node_modules",
}


PACKAGE_PATTERN = re.compile(
    r"^\s*package\s+([\w.]+)\s*;",
    re.MULTILINE,
)


CLASS_PATTERN = re.compile(
    r"\b(?:class|interface|enum|record)\s+([A-Za-z_$][\w$]*)"
)


PUBLIC_METHOD_PATTERN = re.compile(
    r"""
    ^[ \t]*
    public
    [ \t\r\n]+

    # Java method modifiers
    (?:
        (?:static|final|synchronized|abstract|native|strictfp|default)
        [ \t\r\n]+
    )*

    # Generic method type parameters, such as: <T extends Object>
    (?:
        <[^;{}()]+>
        [ \t\r\n]*
    )?

    # Return type, including generic and array types
    (?P<return_type>
        [A-Za-z_$@]
        [\w$.\[\]<>?,\s@&]*
    )
    [ \t\r\n]+

    # Method name
    (?P<method_name>[A-Za-z_$][\w$]*)

    [ \t\r\n]*
    \(
        (?P<parameters>.*?)
    \)

    [ \t\r\n]*

    # Optional throws declaration
    (?:
        throws
        [^{;]+
    )?

    [ \t\r\n]*
    [;{]
    """,
    re.MULTILINE | re.DOTALL | re.VERBOSE,
)


def should_ignore(path: Path) -> bool:
    """
    Returns True if the path belongs to an ignored directory.
    """
    return any(part in IGNORED_DIRECTORIES for part in path.parts)


def remove_comments_and_literals(source: str) -> str:
    """
    Removes comments and masks string/character literals while preserving
    line breaks. Preserving line breaks keeps source parsing more reliable.
    """

    pattern = re.compile(
        r"""
        # Java text block: triple quotes
        (?P<text_block>\"\"\".*?\"\"\")

        |

        # Normal Java string
        (?P<string>"(?:\\.|[^"\\])*")

        |

        # Java character literal
        (?P<char>'(?:\\.|[^'\\])*')

        |

        # Single-line comment
        (?P<single_comment>//[^\n]*)

        |

        # Multi-line comment
        (?P<multi_comment>/\*.*?\*/)
        """,
        re.DOTALL | re.VERBOSE,
    )

    def replace_match(match: re.Match) -> str:
        value = match.group(0)

        # Keep newline characters, replace everything else with spaces.
        return "".join("\n" if char == "\n" else " " for char in value)

    return pattern.sub(replace_match, source)


def normalize_whitespace(value: str) -> str:
    """
    Converts multiple spaces/newlines/tabs into one space.
    """
    return re.sub(r"\s+", " ", value).strip()


def extract_package(source: str) -> str:
    """
    Extracts the package name from Java source.
    """
    match = PACKAGE_PATTERN.search(source)

    if match:
        return match.group(1)

    return "(default package)"


def extract_class_names(source: str) -> set[str]:
    """
    Extracts class/interface/enum/record names.
    This is used to distinguish constructors from methods.
    """
    return set(CLASS_PATTERN.findall(source))


def extract_public_methods(source: str) -> list[dict]:
    """
    Extracts explicitly public methods.

    Constructors are excluded because they do not have a return type.
    """
    cleaned_source = remove_comments_and_literals(source)
    class_names = extract_class_names(cleaned_source)

    methods = []

    for match in PUBLIC_METHOD_PATTERN.finditer(cleaned_source):
        return_type = normalize_whitespace(match.group("return_type"))
        method_name = match.group("method_name")
        parameters = normalize_whitespace(match.group("parameters"))

        # Prevent class/record declarations from being detected as methods.
        invalid_return_type_keywords = {
            "class",
            "interface",
            "enum",
            "record",
        }

        return_type_tokens = set(return_type.split())

        if return_type_tokens & invalid_return_type_keywords:
            continue

        # Constructors are not methods and are excluded.
        if method_name in class_names:
            continue

        # Avoid probable field initializers such as:
        # public String value = createValue();
        complete_declaration = match.group(0)
        before_parenthesis = complete_declaration.split("(", 1)[0]

        if "=" in before_parenthesis:
            continue

        signature = f"{method_name}({parameters})"

        methods.append(
            {
                "name": method_name,
                "return_type": return_type,
                "parameters": parameters,
                "signature": signature,
            }
        )

    # Remove accidental duplicate matches while preserving order.
    unique_methods = []
    seen = set()

    for method in methods:
        identity = (
            method["return_type"],
            method["name"],
            method["parameters"],
        )

        if identity not in seen:
            seen.add(identity)
            unique_methods.append(method)

    return unique_methods


def find_java_files(project_root: Path) -> list[Path]:
    """
    Finds all Java files in the project except ignored directories.
    """
    java_files = []

    for path in project_root.rglob("*.java"):
        relative_path = path.relative_to(project_root)

        if should_ignore(relative_path):
            continue

        java_files.append(path)

    return sorted(java_files)


def generate_report(project_root: Path, output_file: Path) -> None:
    java_files = find_java_files(project_root)

    packages = defaultdict(list)
    total_methods = 0
    unreadable_files = []

    for java_file in java_files:
        try:
            source = java_file.read_text(encoding="utf-8")
        except UnicodeDecodeError:
            try:
                source = java_file.read_text(
                    encoding="utf-8",
                    errors="replace",
                )
            except OSError as error:
                unreadable_files.append((java_file, str(error)))
                continue
        except OSError as error:
            unreadable_files.append((java_file, str(error)))
            continue

        package_name = extract_package(source)
        methods = extract_public_methods(source)

        total_methods += len(methods)

        packages[package_name].append(
            {
                "name": java_file.name,
                "path": java_file.relative_to(project_root),
                "methods": methods,
            }
        )

    output_file.parent.mkdir(parents=True, exist_ok=True)

    with output_file.open("w", encoding="utf-8") as output:
        output.write("JAVA PROJECT STRUCTURE REPORT\n")
        output.write("=" * 80 + "\n\n")

        output.write(f"Project root   : {project_root.resolve()}\n")
        output.write(f"Packages       : {len(packages)}\n")
        output.write(f"Java files     : {len(java_files)}\n")
        output.write(f"Public methods : {total_methods}\n\n")

        output.write("=" * 80 + "\n\n")

        for package_name in sorted(packages):
            output.write(f"PACKAGE: {package_name}\n")
            output.write("-" * 80 + "\n")

            package_files = sorted(
                packages[package_name],
                key=lambda file_info: str(file_info["path"]),
            )

            for file_info in package_files:
                output.write(f"\n  FILE: {file_info['name']}\n")
                output.write(f"  PATH: {file_info['path']}\n")

                methods = file_info["methods"]

                if not methods:
                    output.write("  PUBLIC METHODS: none\n")
                    continue

                output.write("  PUBLIC METHODS:\n")

                for method in methods:
                    output.write(
                        f"    - {method['return_type']} "
                        f"{method['signature']}\n"
                    )

            output.write("\n\n")

        if unreadable_files:
            output.write("=" * 80 + "\n")
            output.write("UNREADABLE FILES\n")
            output.write("=" * 80 + "\n\n")

            for file_path, error_message in unreadable_files:
                output.write(f"- {file_path}: {error_message}\n")

    print("Report generated successfully.")
    print(f"Project root : {project_root.resolve()}")
    print(f"Output file  : {output_file.resolve()}")
    print(f"Packages     : {len(packages)}")
    print(f"Java files   : {len(java_files)}")
    print(f"Public methods: {total_methods}")


def main() -> None:
    parser = argparse.ArgumentParser(
        description=(
            "Generate a report containing Java packages, files, "
            "and explicitly public methods."
        )
    )

    parser.add_argument(
        "project_path",
        nargs="?",
        default=".",
        help="Path to the Java project. Default: current directory",
    )

    parser.add_argument(
        "-o",
        "--output",
        default="java-project-report.txt",
        help="Output report file. Default: java-project-report.txt",
    )

    arguments = parser.parse_args()

    project_root = Path(arguments.project_path).expanduser().resolve()

    if not project_root.exists():
        raise SystemExit(f"Project path does not exist: {project_root}")

    if not project_root.is_dir():
        raise SystemExit(f"Project path is not a directory: {project_root}")

    output_file = Path(arguments.output).expanduser()

    if not output_file.is_absolute():
        output_file = project_root / output_file

    generate_report(project_root, output_file)


if __name__ == "__main__":
    main()
