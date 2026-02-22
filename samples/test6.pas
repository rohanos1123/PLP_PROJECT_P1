program SealedInheritance;

type
    TTest = class
        value : integer;
        constructor Create(val : integer);
    end;

    TChild = class sealed (TTest)
        otherVal : integer;
        constructor Create(val : integer);
    end;

    TGrandchild = class(TChild);

constructor TTest.Create(val : integer);
begin
    value := val;
end;

constructor TChild.Create(val : integer);
begin
    value := val;
    otherVal := 21;
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

    furtherTest := TChild.Create(567);
    furtherTest.otherVal := -21;
    WriteLn(furtherTest.value);
    WriteLn(furtherTest.otherVal);
end.