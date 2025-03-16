package {{packageName}};

public record {{className}}({% for entry in entryList %}{{entry['Type']}} {{entry['Field']}}, {% endfor %}Object... params) {
}
