program Encapsulation;

type
    TTest = class
        value : integer;
        constructor Create(val : integer);
        private
        var
            priv : integer;
    end;

constructor TTest.Create(val : integer);
begin
    value := val;
    priv := 999;
end;

var
    test : TTest;

begin

    test := TTest.Create(47);

    WriteLn(test.priv);

end.