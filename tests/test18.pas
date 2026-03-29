program BreakAndContinue;

var
    i : integer;

begin
    for i := 0 to 7 do
    begin
        if i = 3 then continue;
        WriteLn(i);
        if i = 5 then break;
    end;

    i := 0;

    WriteLn('Loop again!');

    while i < 12 do
    begin
        i := i - 2;
        WriteLn(i);
        if i > -10 then continue;
        WriteLn('Oops! Wrong way! Exiting...');
        break;
    end;
end.