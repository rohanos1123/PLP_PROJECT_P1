program MultiInterface;

type
    IPrintBase = interface
        procedure print;
    end;

    IPrint = interface
        procedure print(int : integer);
    end;

    TTest = class
        value : integer;
        constructor Create(val : integer);
    end;

    TChild = class (TTest)
        otherVal : integer;
        constructor Create(val : integer);
    end;

    TGrandchild = class(TChild, IPrint, IPrintBase);

constructor TTest.Create(val : integer);
begin
    value := val;
end;

constructor TChild.Create(val : integer);
begin
    value := val;
    otherVal := 21;
end;

procedure TGrandchild.print(int : integer);
begin
    WriteLn(int);
end;

procedure TGrandchild.print;
begin
    WriteLn(607);
end;

var
    test : TChild;
    furtherTest : TGrandchild;

begin

    test := TChild.Create(47);

    WriteLn(test.value);
    WriteLn(test.otherVal);

    test.otherVal := 87;
    WriteLn(test.otherVal);

    furtherTest := TGrandchild.Create(567);
    furtherTest.otherVal := -21;
    WriteLn(furtherTest.value);
    WriteLn(furtherTest.otherVal);

    furtherTest.print(7);
    furtherTest.print;
end.