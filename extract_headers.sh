#!/bin/bash

# Source directory (where to search for .h files)
SOURCE_DIR="$1"

# Destination directory (where to save extracted .h files)
DEST_DIR="$2"

# Check if source and destination directories are provided
if [[ -z "$SOURCE_DIR" || -z "$DEST_DIR" ]]; then
    echo "Usage: $0 <source_directory> <destination_directory>"
    exit 1
fi

# Create the destination directory if it doesn't exist
mkdir -p "$DEST_DIR"

echo "🔍 Scanning for .h files in: $SOURCE_DIR"

# Find and copy all .h files directly to DEST_DIR
found_files=0
find "$SOURCE_DIR" -type f -name "*.h" | while read -r file; do
    found_files=$((found_files + 1))
    filename=$(basename "$file")

    # Copy the file to the destination
    cp "$file" "$DEST_DIR/$filename"

    echo "✅ Copied: $filename"
done

 echo "🎉 Successfully copied $found_files header files to: $DEST_DIR"