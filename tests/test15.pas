program LoopInProcedure; 

var 
    z : integer; 

procedure loopTest; 
    var 
        i : integer; 

    begin
        for i := 0 to z do
        begin
            WriteLn(i);
        end;
    end;  

begin 
    z := 10; 
    loopTest; 
end. 