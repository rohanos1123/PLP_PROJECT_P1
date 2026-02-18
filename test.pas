program SimpleObject;

{$APPTYPE CONSOLE}

type
  TDog = class
    Name: string;
    procedure Bark;
  end;

procedure TDog.Bark;
begin
  WriteLn(Name, ' says: Woof!');
end;

var
  MyDog: TDog;

begin
  MyDog := TDog.Create;
  MyDog.Name := 'Buddy';
  MyDog.Bark;
  MyDog.Free;

  ReadLn;
end.
