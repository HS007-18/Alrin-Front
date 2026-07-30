def replace_in_file(path, old, new):
    with open(path, 'r') as f:
        content = f.read()
    with open(path, 'w') as f:
        f.write(content.replace(old, new))

replace_in_file('app/build.gradle.kts', 'storeFile = file("${rootDir}/debug.keystore")', 'storeFile = file("$rootDir/debug.keystore")')
