program SimpleClassWithConstructor;

{$APPTYPE CONSOLE}

type
  TCar = class
    private
        number: integer;

    public
        idk: integer;
        constructor Create(AColor: integer);
  end;

  constructor TCar.Create(AColor: integer);
  begin
    Color := AColor;
  end;

var
   Num : integer;
   Crack : integer;


begin
  Num := 50;
  Crack := 74;

  WriteLn(Num);
  WriteLn(Crack);

end.