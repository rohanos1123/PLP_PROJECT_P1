program Infinite;

var
    i : Integer;

begin
    for i := 0 to 10 do
    begin
        WriteLn(i);
    end;

    WriteLn(9999);

    for i := 8 downto 2 do
    begin
        WriteLn(i);
    end;

    while true do
    begin
        WriteLn(607);
        break;
        WriteLn(999);
    end;
end.