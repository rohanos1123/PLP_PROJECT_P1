program Interfaces;

type
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

    TGrandchild = class(TChild, IPrint);

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

var
    test : TChild;
    furtherTest : TGrandchild;

begin

    furtherTest := TGrandchild.Create(567);
    furtherTest.print(7);
end.