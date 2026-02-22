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
        constructor Create(AColor: integer; ACum : integer);
  end;
  constructor TCar.Create(AColor: integer; ACum: integer);
  begin
    idk := AColor;
    cum := ACum;
    number := 9234;
  end;


  type
    TBob = class
      public
          KIDS: integer;
          constructor Create(K : Integer);
          constructor Create;
          destructor Destroy;
    end;

    constructor TBob.Create;
    begin
        KIDS := 4192;
    end;

    destructor TBob.Destroy;
    begin
      WriteLn(999);
    end;



var
   Khalil : TCar;
   Num : integer;
   Crack : integer;
   Omar : TCar;
   Taco : TBob;


begin

    Num := 53;
    Crack := 42;

    Khalil := TCar.Create(Num, Crack);
    Omar := TCar.Create(32, 81);

    {This works now}
    Taco := TBob.Create;

    WriteLn(Omar.cum);
    WriteLn(Khalil.cum);
    ReadLn(Khalil.cum);
    WriteLn(Khalil.cum);
    WriteLn(Omar.cum);

    {Should be 721}
    WriteLn(Taco.KIDS);

    Taco.Destroy;

    Taco := TBob.Create;

    WriteLn(Taco.KIDS);

    {Should throw a priate access error}
    {WriteLn(Omar.number);}




end.