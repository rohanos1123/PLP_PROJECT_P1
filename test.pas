program SimpleClassWithConstructor;

{$APPTYPE CONSOLE}

type
  TCar = class
    private
        number: integer;
        lamario : integer;

    public
        idk: integer;
        cum : integer;
        constructor Create(AColor: integer);
  end;

  constructor TCar.Create(AColor: integer; ACum: integer);
  begin
    idk := AColor;
    cum := ACum;
  end;

var
   Khalil : TCar;
   Num : integer;
   Crack : integer;
   Omar : TCar;


begin
    Num := 53;
    Crack := 42;

    Khalil := TCar.Create(Num, Crack);
    Omar := TCar.Create(32, 81);
    WriteLn(Khalil.cum);
    WriteLn(Omar.cum);

    {Should Error when uncommented due to private access}
    {WriteLn(Khalil.number);}
    {WriteLn(Khalil.lamario);}


end.