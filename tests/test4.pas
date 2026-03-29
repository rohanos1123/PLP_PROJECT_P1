program ConstructorsAndDestructors;

type
    TTest = class
        value : integer;
        constructor Create(val : integer);
        destructor Destroy;
        private
        var
            priv : integer;
    end;

constructor TTest.Create(val : integer);
begin
    value := val;
    priv := 999;
end;

destructor TTest.Destroy;
begin
    WriteLn(priv);
end;

var
    test : TTest;

begin

    test := TTest.Create(47);

    test.Destroy;

    WriteLn(test.value);

end.