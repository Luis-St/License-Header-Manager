# License Manager (LM)

A Gradle plugin for managing license headers in source code files.\
This plugin helps you automatically add, update, and verify license headers across your project's source files.

## Features

- **Add license headers** to source files automatically
- **Check existing headers** for compliance
- **Variable substitution** in header templates
- **Flexible file patterns** for inclusion/exclusion and definition of source sets
- **Multiple line ending support** (LF/CRLF)
- **Gradle task integration** with proper incremental builds
- **Configurable spacing** after headers

## Installation

```kotlin
// In your settings.gradle.kts
pluginManagement {
	plugins {
		id("net.luis.lm") version "<latest-version>"
	}
	
	repositories {
		gradlePluginPortal()
		mavenCentral()
		maven {
			url = uri("https://maven.luis-st.net/plugins/")
		}
	}
}

// In your build.gradle.kts
plugins {
	id("net.luis.lm")
}
```

## Configuration

Configure the plugin in your `build.gradle.kts`:

```kotlin
licenseManager {
    headerFile = "license-header.txt"           // Header template file (default: "header.txt")
    lineEnding = LineEnding.LF                  // Line ending type (default: LF)
    spacingAfterHeader = 2                      // Empty lines after header (default: 1)
    
    // Variable substitution
    variable("year", "2024")
    variable("author", "Your Name")
    variable("projectName", project.name)
    
    // Source set definitions
	sourceSets = listOf("main", "test") // Default source sets to process (default: ["main"])
    
    // File patterns (supports Ant-style patterns)
    include("**/*.java", "**/*.kt", "**/*.scala")
    exclude("**/generated/**", "**/*.generated.kt")
}
```

### Configuration Options

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `headerFile` | String | `"header.txt"` | Path to the license header template file |
| `lineEnding` | LineEnding | `LineEnding.LF` | Line ending style (`LF` or `CRLF`) |
| `spacingAfterHeader` | Int | `1` | Number of blank lines after the header |
| `variables` | Map<String, String> | `{}` | Variables for template substitution |
| `sourceSets` | List<String> | `["main"]` | Source sets to process |
| `includes` | List<String> | `[]` | File patterns to include |
| `excludes` | List<String> | `[]` | File patterns to exclude |

## Usage

### Available Tasks

The plugin provides two main tasks:

#### `updateLicenses`
Adds or updates license headers in matching source files.

```bash
./gradlew updateLicenses
```

#### `checkLicenses`
Verifies that all matching source files have proper license headers. Fails the build if any files are missing headers.

```bash
./gradlew checkLicenses
```

Both tasks are automatically added to the `license` task group.

## Header Template

Create a header template file (e.g., `header.txt`) in your project root:

```text
Copyright (c) ${year} ${author}

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

### Variable Substitution

The plugin supports two variable substitution formats:

- `${variableName}` - Standard format
- `{{variableName}}` - Alternative format

Variables are defined in the configuration:

```kotlin
licenseHeaderManager {
    variable("year", "2024")
    variable("author", "John Doe")
    variable("company", "ACME Corp")
}
```

## Source Sets

You can specify which source sets to process. By default, only the `main` source set is included.

```kotlin
licenseManager {
    sourceSets = listOf("main", "test", "integrationTest")
}
```

## File Patterns

The plugin uses Ant-style glob patterns for file matching:

- `**/*.java` - All Java files in any directory
- `src/main/**/*.kt` - All Kotlin files under src/main
- `**/generated/**` - All files in any "generated" directory
- `**/*.{java,kt,scala}` - Multiple extensions (not directly supported, use multiple patterns)

### Examples

```kotlin
licenseManager {
    // Include specific file types
    include("**/*.java", "**/*.kt", "**/*.scala", "**/*.groovy")
    
    // Exclude generated files and test resources
    exclude(
        "**/generated/**",
        "**/build/**", 
        "**/*.generated.*",
        "**/test/resources/**"
    )
}
```

## Integration with CI/CD

Add header checking to your CI pipeline:

```yaml
# GitHub Actions example
- name: Check License Headers
  run: ./gradlew checkLicenseHeaders
```

You can also automatically fix headers:

```yaml
- name: Add License Headers
  run: ./gradlew updateLicenses
```

## Advanced Configuration

### Multiple Header Files

For projects with different license requirements:

```kotlin
// Create separate configurations for different modules
subprojects {
    licenseManager {
        when (project.name) {
            "public-api" -> headerFile = "headers/apache-2.0.txt"
            "internal-tools" -> headerFile = "headers/proprietary.txt"
            else -> headerFile = "headers/default.txt"
        }
    }
}
```

### Custom Line Endings

```kotlin
licenseManager {
    // Use CRLF for Windows compatibility
    lineEnding = LineEnding.CRLF
    
    // Or LF for Unix/Linux (default)
    lineEnding = LineEnding.LF
}
```

## Troubleshooting

### Common Issues

1. **Header file not found**
   ```
   Header file not found: header.txt
   ```
    - Ensure the header file exists in the specified location
    - Check the `headerFile` configuration path

2. **Files not being processed**
    - Verify your include/exclude patterns
    - Check that files exist in the `src` directory
    - Ensure file extensions match your patterns

3. **Variable substitution not working**
    - Verify variable names match exactly (case-sensitive)
    - Check that you're using correct syntax: `${variable}` or `{{variable}}`

### Debug Information

Enable Gradle info logging to see which files are being processed:

```bash
./gradlew updateLicenses --info
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.
See the header template for the full license text.

## Author

**Luis-St** - *Initial work*
