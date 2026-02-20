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
    number := 607;
    cum := ACum;
    lamario := 400;
  end;

var
   Khalil : TCar;
   Num : integer;
   Crack : integer;


begin
    {
        Crack := 67;
        ReadLn(Num);

        Khalil := TCar.Create(Num, Crack);
        WriteLn(Khalil.idk);
        WriteLn(Khalil.cum);


        Khalil.idk := 7192;

        WriteLn(Khalil.idk);
    }

    Crack := 67;
    Num := 51;
    Khalil := TCar.Create(Num, Crack);
    WriteLn(Khalil.cum);
    ReadLn(Khalil.cum);
    WriteLn(Khalil.cum);


    {Should Error when uncommented due to private access}
    WriteLn(Khalil.number);
    {WriteLn(Khalil.lamario);}


end.