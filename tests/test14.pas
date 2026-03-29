program doubleNested; 


var 
    z : integer;


procedure outer;
var 
    y : integer;
    x : integer;
    procedure medium;
        var 
            y : integer;
            procedure inner; 
            begin 

                {change outer's x}
                x := 10; 
                
                {Change medium's y}
                y := 8;

                {change global z}
                z := 30; 
            end;
            procedure inner2; 
            begin
                {change mediums y}
                y := 51; 
            end;
        begin
            {set medium's y}
            y := 15; 
            inner; 
            inner2;

            {print medium's y}
            WriteLn(y);
        end;
    begin
        {set outer's x}
        x := 81; 

        {set outer's y}
        y := 607; 

        medium; 

        {print outer's x}
        WriteLn(x); 

        {print outer's y}
        WriteLn(y); 
    end;




begin
    {set global z}
    z := 50; 
    
    outer; 

    {print global z}
    WriteLn(z); 
end. 

{Final result = 51, 10, 607, 30}