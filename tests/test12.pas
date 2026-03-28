program parentScopeTest;

procedure outer;
var
    x : integer;

    procedure inner;
    begin
        x := 10;
        WriteLn(x);
    end;

begin
    x := 15;
    inner;
    WriteLn(x);
end;

var
 x : integer;

 begin
    x := 500;
    outer;
    WriteLn(x);
 end.