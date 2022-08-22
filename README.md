# json
A very simple [JSON](https://www.json.org/) parsing and stringifying library with defaults that I like.

There are many much more tested, used, advanced, and customizable libraries for this out there. This was built as an excercise for me and my brain and unless you have a good reason you should probably use one of those instead.

## Some choices made, and why
- Numbers are parsed into as BigDecimals, it's the best fit for the JSON definition of a number and the only type of decimal value you should use in Java.
- Parsing is strict, e.g. `'` is not allowed to define strings. It's a simple specification, it's not difficult to stay within it.
- Optionals are stringified as null or the object it contains. Since all fields in the resulting JSON objects basically behave as optional when used in JavaScript this seems like a good idea to me.
- Null values in objects are skipped when stringified. As above, null or undefined in JavaScript doesn't really matter which.
- Stringifying a parsed object removes all whitespace. I care about the data here, pretty printing is not really a concern.
- Reads a complete string and outputs a complete object. It does not read streams or output object events. Parsing a huge JSON stream where neither the input or the output fits in memory is an extreme edge case that should probably be avoided to begin with.
- Time objects like Instant, LocalDateTime, and so on are stringified as a string. Since browsers easily parse the format they output this seems like a good idea to me.
- No external runtime libraries. Since this began as an excercise for me I wanted to do it all. Also, simple things like this should not need any extra dependencies.
