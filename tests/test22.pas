program RealsTest; 

var
    num1 : real; 
    num2 : real; 
    floatSum : real; 

begin
    num1 := 3.1415926; 
    num2 := 2.71828;  

    floatSum := num1 + num2;
    WriteLn('pi + e:', floatSum); 
    floatSum := num1 * num2;
    WriteLn('pi * e:', floatSum); 
    floatSum := num1 - num2; 
    WriteLn('pi - e:', floatSum); 
    floatSum := num1 / num2; 
    WriteLn('pi / e:', floatSum); 
end.


