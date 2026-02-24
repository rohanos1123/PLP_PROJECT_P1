program Echo;

type
    echo = procedure;

procedure echo;
var
   input : integer;
begin
    ReadLn(input);
    WriteLn(input);
end;

begin
    echo;
end.