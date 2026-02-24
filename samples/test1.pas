program VariableAndFunctionIO;

var
   output : integer;
   input : integer;

type
    concat = procedure (a, b : integer);
    getValue = function : integer;

procedure concat(a, b : integer);
begin
    WriteLn(a, b);
end;

function getValue : integer;
begin
    Result := 10
end;

begin

    output := getValue;

    ReadLn(input);

    concat(output, input);

end.