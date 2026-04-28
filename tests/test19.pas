program AndTest;
    
var
    i : integer;
    k : boolean; 
    m : integer; 

begin
    i := 10;
    m := 15;
    k := not False;  
    if ((m >= i) and (1 > 11)) or k then
        begin 
            WriteLn('Good');  
        end
    else
        begin
            WriteLn('Bad');  
        end;
end. 