program invalidLogicalUse; 

var 
    i : integer; 
    k : real; 
    j : string; 

begin 
    i := 10; 
    k := 12.21 + 21.21; 

    if (i + 43) or (k < 50.31) then WriteLn('Should not print'); 

end. 