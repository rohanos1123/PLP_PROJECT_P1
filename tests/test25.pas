program ForFailure2;
    
var
    c1 : integer;
    c2 : integer; 
    k : real;

begin
    c1 := 0;
    c2 := 15; 
    k := 3.14; 

    {Should loop to 20 to cover both c1 and c2}

    for k := 1.43 to c2 do
        WriteLn('Should not print'); 

end. 