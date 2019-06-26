#!/usr/bin/env bash
cd "$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")"

find ./module_*/ -name '__pycache__' -or -name '*.py[cod]' -delete
