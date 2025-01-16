#!/bin/bash

# Enable error handling: exit on any command failure
set -e

# Save the current directory
original_dir=$(pwd)

echo "Changing directory to 'code-gen'..."
cd code-gen

echo "Running npm install..."
npm install

echo "Removing all files from the 'out' directory..."
rm -rf out/*

echo "Running 'npm run start'..."
npm run start

gen_directory="../src/main/java/org/openhab/binding/matter/internal/client/model/cluster/gen/"
echo "Removing all files from '${gen_directory}'..."
rm -rf "${gen_directory:?}"*

echo "Moving all files from 'out' to '${gen_directory}'..."
mkdir -p "${gen_directory}" # Ensure target directory exists
mv out/* "${gen_directory}"

echo "Changing back to the original directory..."
cd "$original_dir"

echo "Running 'mvn spotless:apply'..."
mvn spotless:apply

echo "Script completed successfully!"
