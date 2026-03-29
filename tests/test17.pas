program Fibonacci;

function fib(n : Integer) : Integer;
begin
    if n <= 1 then Result := n
    else Result := fib(n - 1) + fib(n - 2);
end;

procedure fib_seq(n : Integer);
var
    i : integer;
begin
    for i := 0 to n - 1 do
        Write(fib(i), ', ');
    WriteLn(fib(n));
end;

var
    n : integer;

begin
    Write('Enter n: ');
    ReadLn(n);
    Write('First n Fibonacci Numbers: ');
    fib_seq(n);
end.