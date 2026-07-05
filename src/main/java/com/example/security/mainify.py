import re
from pathlib import Path

ROOT = Path(".")
OUTPUT_DIR = ROOT / "output"
OUTPUT_FILE = OUTPUT_DIR / "Main.java"

EXCLUDED_DIRS = {".idea", ".vscode", ".git", "out", "output"}

OUTPUT_DIR.mkdir(exist_ok=True)

java_files = [
    f for f in ROOT.rglob("*.java")
    if not any(part in EXCLUDED_DIRS for part in f.parts)
]

main_file = None
other_files = []

for file in java_files:
    if file.stem == "Main":
        main_file = file
    else:
        other_files.append(file)

java_files = sorted(other_files)
if main_file:
    java_files.append(main_file)


def is_standard_import(imp: str) -> bool:
    return (
            imp.startswith("java.") or
            imp.startswith("javax.") or
            imp.startswith("jakarta.") or
            imp.startswith("org.w3c.") or
            imp.startswith("org.xml.")
    )


all_imports = set()
combined_code = []

for file in java_files:
    content = file.read_text(encoding="utf-8")

    content = re.sub(
        r"^\s*package\s+[^;]+;\s*\n?",
        "",
        content,
        flags=re.MULTILINE
    )

    content = re.sub(
        r"\bpublic\s+(abstract\s+)?(class|interface|enum|record)\s+(?!Main\b)",
        lambda m: (m.group(1) or "") + m.group(2) + " ",
        content
    )

    code_lines = []

    for line in content.splitlines():
        stripped = line.strip()

        if stripped.startswith("import "):
            match = re.match(r"import\s+([\w.]+(?:\.\*)?);", stripped)
            if match:
                imp = match.group(1)
                if is_standard_import(imp):
                    all_imports.add(stripped)
            continue

        code_lines.append(line)

    combined_code.append("\n".join(code_lines).strip())

with OUTPUT_FILE.open("w", encoding="utf-8") as f:
    for imp in sorted(all_imports):
        f.write(imp + "\n")

    f.write("\n")

    f.write("\n\n".join(combined_code))

print(f"Generated {OUTPUT_FILE}")
