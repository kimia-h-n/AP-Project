#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="${1:-src/main/java}"

if [[ ! -d "$ROOT_DIR" ]]; then
    echo "خطا: پوشه '$ROOT_DIR' وجود ندارد." >&2
    exit 1
fi

find "$ROOT_DIR" -type d -print0 |
while IFS= read -r -d '' package_dir; do
    mapfile -d '' java_files < <(
        find "$package_dir" -maxdepth 1 -type f -name '*.java' -print0 | sort -z
    )

    if (( ${#java_files[@]} > 0 )); then
        package_name="${package_dir#"$ROOT_DIR"/}"
        package_name="${package_name//\//.}"

        [[ "$package_dir" == "$ROOT_DIR" ]] && package_name="(root package)"

        echo "Package: $package_name"

        for java_file in "${java_files[@]}"; do
            echo "  - $(basename "$java_file")"
        done

        echo
    fi
done
