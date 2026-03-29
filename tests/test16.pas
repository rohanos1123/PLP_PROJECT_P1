program Recursion;

var
    z : Integer;

procedure space(max : Integer);
var
    i : Integer;
begin
    for i := 0 to max do
        Write(' ');
end;

procedure f;
var
    x : Integer;

    procedure g;
    var
        y : Integer;
    begin
        y := x;
        space(y);
        WriteLn(y);
        if x < 10 then
        begin
            x := x + 1;
            g;
            space(y);
            WriteLn(y);
        end;
    end;

begin
    x := z;
    g
end;

begin
    z := 1;
    f
end.