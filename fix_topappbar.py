def replace_in_file(path, old, new):
    with open(path, 'r') as f:
        content = f.read()
    with open(path, 'w') as f:
        f.write(content.replace(old, new))

replace_in_file('app/src/main/java/com/aistudio/alrinkz/xzyy/ChatScreen.kt', 
                '@Composable\nfun ChatScreen', 
                '@OptIn(ExperimentalMaterial3Api::class)\n@Composable\nfun ChatScreen')
