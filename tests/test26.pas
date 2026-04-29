program ifFailure;

var
    value : integer; 

begin
    value := 15; 
    if (value + 10) then
        begin
            WriteLn('Error thrown before');
        end;
end. 