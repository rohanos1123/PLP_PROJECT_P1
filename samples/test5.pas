program Methods;

type
    TTest = class
    private
        value : integer;
    public
        constructor Create;
        function getValue : integer;
        procedure print8;
    end;

constructor TTest.Create;
begin
    value := 11;
end;

procedure TTest.print8;
begin
    WriteLn(8);
end;

function TTest.getValue : integer;
begin
    Result := value;
end;

var
    test : TTest;

begin

    test := TTest.Create;

    test.print8;

    WriteLn(test.getValue);

end.