#!/usr/bin/env python3
"""Validate local links in versioned Markdown without third-party packages."""

from __future__ import annotations

import re
import sys
from pathlib import Path
from urllib.parse import unquote


ROOT = Path(__file__).resolve().parents[1]
LINK_PATTERN = re.compile(r"!?\[[^\]]*\]\(([^)]+)\)")
IGNORED_PREFIXES = ("http://", "https://", "mailto:", "tel:", "data:")


def iter_markdown_files() -> list[Path]:
    ignored_parts = {"node_modules", "dist", ".angular", "build", ".git"}
    return sorted(
        path
        for path in ROOT.rglob("*.md")
        if not ignored_parts.intersection(path.relative_to(ROOT).parts)
    )


def normalize_target(raw_target: str) -> str:
    target = raw_target.strip()
    if target.startswith("<") and target.endswith(">"):
        target = target[1:-1]
    if " \"" in target:
        target = target.split(" \"", 1)[0]
    return unquote(target.split("#", 1)[0])


def main() -> int:
    errors: list[str] = []
    files = iter_markdown_files()

    for markdown in files:
        text = markdown.read_text(encoding="utf-8")
        for match in LINK_PATTERN.finditer(text):
            raw_target = match.group(1).strip()
            if raw_target.startswith("#") or raw_target.lower().startswith(IGNORED_PREFIXES):
                continue

            target = normalize_target(raw_target)
            if not target:
                continue
            if Path(target).suffix.lower() == ".pdf":
                errors.append(f"{markdown.relative_to(ROOT)}: PDF link is not versioned: {raw_target}")
                continue

            resolved = (ROOT / target.lstrip("/")) if target.startswith("/") else (markdown.parent / target)
            if not resolved.resolve().exists():
                errors.append(f"{markdown.relative_to(ROOT)}: missing link target: {raw_target}")

    if errors:
        print("Documentation validation failed:")
        for error in errors:
            print(f"- {error}")
        return 1

    print(f"Documentation validation passed: {len(files)} Markdown files checked.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
