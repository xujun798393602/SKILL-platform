#!/bin/bash
# Docker build wrapper for FUSE filesystem
# The project is on vmhgfs-fuse which Docker can't read for build context.
# This script copies to /tmp and builds from there.

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BUILD_DIR="/tmp/skill-platform-build"

echo "==> Copying project to $BUILD_DIR ..."
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"
# Use tar to copy, which gracefully skips FUSE ghost entries (backslash-path files)
(cd "$SCRIPT_DIR" && tar --exclude='__pycache__' -cf - . 2>/dev/null) | (cd "$BUILD_DIR" && tar -xf - 2>/dev/null) || true

echo "==> Building from $BUILD_DIR (source: $SCRIPT_DIR) ..."
cd "$BUILD_DIR"
docker compose "$@"

echo ""
echo "==> Done."
echo "    Source:  $SCRIPT_DIR (shared directory)"
echo "    Build:   $BUILD_DIR (Docker context, auto-managed)"
echo "    Access:  http://localhost:8080"
echo ""
echo "    Note: Re-run this script after any code changes in $SCRIPT_DIR"
