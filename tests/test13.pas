program Infinite;

var
    i : Integer;

begin
    for i := 0 to 10 do
    begin
        WriteLn(i);
        continue;
        WriteLn(20);
    end;

    WriteLn('FOR COMPLETE');

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