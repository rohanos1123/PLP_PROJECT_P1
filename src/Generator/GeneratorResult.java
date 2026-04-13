package Generator;

import java.util.ArrayList;

public sealed interface GeneratorResult permits Immediate, ImmediateList, Identifier, IdentifierList { }

final record ImmediateList(ArrayList<Immediate> value) implements GeneratorResult { }
final record Identifier(String value) implements GeneratorResult { }
final record IdentifierList(ArrayList<String> value) implements GeneratorResult { }