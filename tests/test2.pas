program ClassesAndObjects;

type
    TTest = class
        value : integer;
        constructor Create(val : integer);
    end;

constructor TTest.Create(val : integer);
begin
    value := val;
end;

var
    test : TTest;

begin

    test := TTest.Create(47);

    WriteLn(test.value);

end.