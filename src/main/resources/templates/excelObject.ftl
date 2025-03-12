package {{packageName}};

public record {{className}}(int sn{% for field in fieldList %}, {{field['type']}} {{field['name']}}{% endfor %}) {
}
