program AndTest;
    
var
    c1 : integer;
    c2 : integer; 
    k : integer;

begin
    c1 := 0;
    c2 := 0; 

    {Should loop to 20 to cover both c1 and c2}

    while (c1 <= 10) and (c2 <= 10) do
        begin
            if (k mod 2) = 0 then
                begin
                    c1 := c1 + 1; 
                end 
            else  
                begin 
                    c2 := c2 + 1;
                end;

            WriteLn(k); 
            k := k + 1; 
        end;

    

end. 