program ForFailure;
    
var
    c1 : integer;
    c2 : integer; 
    k : real;

begin
    c1 := 0;
    c2 := 0; 
    k := 3.14; 

    {Should loop to 20 to cover both c1 and c2}

    for c1 := 0 to k do
        WriteLn('Should not print'); 

end. 