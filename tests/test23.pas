program ChrTestWithFunction; 

var
    filteredChoice : char; 
    input : integer; 

begin
    while True do 
        begin
            ReadLn(input);

            if input = 0 then filteredChoice := Chr(78)
            else if input = 1 then filteredChoice := Chr(89)
            else filteredChoice := Chr(67); 

            {Test comparison operators on Chr Function}
            if not (filteredChoice = Chr(67)) then
                begin
                    WriteLn('Filtered Choice: ', filteredChoice); 
                    break
                end
            else 
                WriteLn('Please type 1 or 0'); 
        end; 

        WriteLn('Thank you for your input!'); 

end. 


