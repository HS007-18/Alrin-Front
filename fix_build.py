def replace_in_file(path, old, new):
    with open(path, 'r') as f:
        content = f.read()
    with open(path, 'w') as f:
        f.write(content.replace(old, new))

replace_in_file('gradle/libs.versions.toml', 'kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }', '')
replace_in_file('build.gradle.kts', 'alias(libs.plugins.kotlin.compose) apply false', '')
replace_in_file('app/build.gradle.kts', 'alias(libs.plugins.kotlin.compose)', '')
