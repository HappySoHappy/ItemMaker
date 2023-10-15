package me.howandev.itemmaker.nbt.parser;


import me.howandev.itemmaker.nbt.NBTString;
import me.howandev.itemmaker.nbt.exceptions.MalformedNBTException;

final class StringParser implements NBTElementParser<NBTString> {

    private static final StringParser QUOTED_STRING_PARSER = new StringParser(true);
    private static final StringParser UNQUOTED_STRING_PARSER = new StringParser(false);

    private final boolean quoted;

    private StringParser(final boolean quoted) {
        this.quoted = quoted;
    }

    @Override
    public NBTString parse(final StringReader reader) throws MalformedNBTException {
        if (quoted)
            return parseQuotedString(reader);
        return parseUnquotedString(reader);
    }

    private NBTString parseQuotedString(final StringReader reader) {
        final char quote = reader.next();
        final StringBuilder stringBuilder = new StringBuilder();
        boolean escaped = false;
        while (reader.canRead()) {
            final char current = reader.read();
            if (current == '\\' && !escaped) {
                escaped = true;
                continue;
            }

            if (current == quote && !escaped)
                return new NBTString(stringBuilder.toString());

            escaped = false;
            stringBuilder.append(current);
        }
        throw new MalformedNBTException("Unterminated quoted literal", reader.getCursor());
    }

    private NBTString parseUnquotedString(final StringReader reader) {
        final String value = reader.readUntil(c ->
                !(Character.isDigit(c)
                        || Character.isAlphabetic(c)
                        || c == '_'
                        || c == '-'
                        || c == '.'
                        || c == '+'));
        if (value.isEmpty())
            throw new MalformedNBTException("Unable to parse unquoted string. Value is empty.", reader.getCursor());
        return new NBTString(value);
    }

    public static StringParser quoted() {
        return QUOTED_STRING_PARSER;
    }

    public static StringParser unquoted() {
        return UNQUOTED_STRING_PARSER;
    }

}
