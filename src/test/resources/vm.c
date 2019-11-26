typedef struct {
    double x;
    double y;
} double2;

int code[] = {30, 0, 50, 30, 4, 25, 0, 0, 4, 0}; // var a = 50; var b = 25; var c = a + b;
int codeSize = 10;

int main() {
    int pc = 0;
    
    int data[1024];
    
    while(pc < codeSize) {
        switch(code[pc]) {            // === Add ===
            // Add: [*Int, *Int]
            case 0: data[code[pc + 3]] = data[code[pc + 1]] + data[code[pc + 2]]; pc += 4; break; 
            // Add: [Int, *Int]
            case 1: data[code[pc + 6]] = code[pc + 1] + data[code[pc + 5]]; pc += 7; break; 
            // Add: [*Real, *Real]
            case 2: *((double*) (&data[code[pc + 3]])) = *((double*) (&data[code[pc + 1]])) + *((double*) (&data[code[pc + 2]])); pc += 4; break; 
            // Add: [Real, *Real]
            case 3: *((double*) (&data[code[pc + 10]])) = *((double*) (&code[pc + 1])) + *((double*) (&data[code[pc + 9]])); pc += 11; break; 
            // Add: [*Cplx, *Cplx]
            case 4: *((double2*) (&data[code[pc + 3]])) = *((double2*) (&data[code[pc + 1]])) + *((double2*) (&data[code[pc + 2]])); pc += 4; break; 
            // Add: [Cplx, *Cplx]
            case 5: *((double2*) (&data[code[pc + 18]])) = *((double2*) (&code[pc + 1])) + *((double2*) (&data[code[pc + 17]])); pc += 19; break; 
            // === Sub ===
            // Sub: [*Int, *Int]
            case 6: data[code[pc + 3]] = data[code[pc + 1]] - data[code[pc + 2]]; pc += 4; break; 
            // Sub: [Int, *Int]
            case 7: data[code[pc + 6]] = code[pc + 1] - data[code[pc + 5]]; pc += 7; break; 
            // Sub: [*Real, *Real]
            case 8: *((double*) (&data[code[pc + 3]])) = *((double*) (&data[code[pc + 1]])) - *((double*) (&data[code[pc + 2]])); pc += 4; break; 
            // Sub: [Real, *Real]
            case 9: *((double*) (&data[code[pc + 10]])) = *((double*) (&code[pc + 1])) - *((double*) (&data[code[pc + 9]])); pc += 11; break; 
            // Sub: [*Cplx, *Cplx]
            case 10: *((double2*) (&data[code[pc + 3]])) = *((double2*) (&data[code[pc + 1]])) - *((double2*) (&data[code[pc + 2]])); pc += 4; break; 
            // Sub: [Cplx, *Cplx]
            case 11: *((double2*) (&data[code[pc + 18]])) = *((double2*) (&code[pc + 1])) - *((double2*) (&data[code[pc + 17]])); pc += 19; break; 
            // === Mul ===
            // Mul: [*Int, *Int]
            case 12: data[code[pc + 3]] = data[code[pc + 1]] * data[code[pc + 2]]; pc += 4; break; 
            // Mul: [Int, *Int]
            case 13: data[code[pc + 6]] = code[pc + 1] * data[code[pc + 5]]; pc += 7; break; 
            // Mul: [*Real, *Real]
            case 14: *((double*) (&data[code[pc + 3]])) = *((double*) (&data[code[pc + 1]])) * *((double*) (&data[code[pc + 2]])); pc += 4; break; 
            // Mul: [Real, *Real]
            case 15: *((double*) (&data[code[pc + 10]])) = *((double*) (&code[pc + 1])) * *((double*) (&data[code[pc + 9]])); pc += 11; break; 
            // Mul: [*Cplx, *Cplx]
            case 16: *((double2*) (&data[code[pc + 3]])) = *((double2*) (&data[code[pc + 1]])) * *((double2*) (&data[code[pc + 2]])); pc += 4; break; 
            // Mul: [Cplx, *Cplx]
            case 17: *((double2*) (&data[code[pc + 18]])) = *((double2*) (&code[pc + 1])) * *((double2*) (&data[code[pc + 17]])); pc += 19; break; 
            // === Div ===
            // Div: [*Int, *Int]
            case 18: data[code[pc + 3]] = data[code[pc + 1]] / data[code[pc + 2]]; pc += 4; break; 
            // Div: [Int, *Int]
            case 19: data[code[pc + 6]] = code[pc + 1] / data[code[pc + 5]]; pc += 7; break; 
            // Div: [*Real, *Real]
            case 20: *((double*) (&data[code[pc + 3]])) = *((double*) (&data[code[pc + 1]])) / *((double*) (&data[code[pc + 2]])); pc += 4; break; 
            // Div: [Real, *Real]
            case 21: *((double*) (&data[code[pc + 10]])) = *((double*) (&code[pc + 1])) / *((double*) (&data[code[pc + 9]])); pc += 11; break; 
            // Div: [*Cplx, *Cplx]
            case 22: *((double2*) (&data[code[pc + 3]])) = *((double2*) (&data[code[pc + 1]])) / *((double2*) (&data[code[pc + 2]])); pc += 4; break; 
            // Div: [Cplx, *Cplx]
            case 23: *((double2*) (&data[code[pc + 18]])) = *((double2*) (&code[pc + 1])) / *((double2*) (&data[code[pc + 17]])); pc += 19; break; 
            // === Mod ===
            // Mod: [*Int, *Int]
            case 24: data[code[pc + 3]] = data[code[pc + 1]] % data[code[pc + 2]]; pc += 4; break; 
            // Mod: [Int, *Int]
            case 25: data[code[pc + 6]] = code[pc + 1] % data[code[pc + 5]]; pc += 7; break; 
            // === Neg ===
            // Neg: [*Int]
            case 26: data[code[pc + 2]] = -data[code[pc + 1]];pc += 3; break; 
            // Neg: [*Real]
            case 27: *((double*) (&data[code[pc + 2]])) = -*((double*) (&data[code[pc + 1]]));pc += 3; break; 
            // Neg: [*Cplx]
            case 28: *((double2*) (&data[code[pc + 2]])) = -*((double2*) (&data[code[pc + 1]]));pc += 3; break; 
            // === Assign ===
            // Assign: [*Int, *Int]
            case 29: data[code[pc + 1]] = data[code[pc + 2]]; pc += 3; break; 
            // Assign: [*Int, Int]
            case 30: data[code[pc + 1]] = code[pc + 2]; pc += 6; break; 
            // Assign: [*Real, *Real]
            case 31: *((double*) (&data[code[pc + 1]])) = *((double*) (&data[code[pc + 2]])); pc += 3; break; 
            // Assign: [*Real, Real]
            case 32: *((double*) (&data[code[pc + 1]])) = *((double*) (&code[pc + 2])); pc += 10; break; 
            // Assign: [*Cplx, *Cplx]
            case 33: *((double2*) (&data[code[pc + 1]])) = *((double2*) (&data[code[pc + 2]])); pc += 3; break; 
            // Assign: [*Cplx, Cplx]
            case 34: *((double2*) (&data[code[pc + 1]])) = *((double2*) (&code[pc + 2])); pc += 18; break; 
            // === Jump ===
            // Jump: [Int]
            case 35: pc = code[pc + 1];break; 
            // === Equal ===
            // Equal: [*Int, *Int]
            case 36: if(data[code[pc + 1]] == data[code[pc + 2]]) pc = code[pc + 3]; else pc = code[pc + 3 + 1];break; 
            // Equal: [Int, *Int]
            case 37: if(code[pc + 1] == data[code[pc + 5]]) pc = code[pc + 6]; else pc = code[pc + 6 + 1];break; 
            // === Less ===
            // Less: [*Int, *Int]
            case 38: if(data[code[pc + 1]] < data[code[pc + 2]]) pc = code[pc + 3]; else pc = code[pc + 3 + 1];break; 
            // Less: [Int, *Int]
            case 39: if(code[pc + 1] < data[code[pc + 5]]) pc = code[pc + 6]; else pc = code[pc + 6 + 1];break; 
            // Less: [*Real, *Real]
            case 40: if(*((double*) (&data[code[pc + 1]])) < *((double*) (&data[code[pc + 2]]))) pc = code[pc + 3]; else pc = code[pc + 3 + 1];break; 
            // Less: [Real, *Real]
            case 41: if(*((double*) (&code[pc + 1])) < *((double*) (&data[code[pc + 9]]))) pc = code[pc + 10]; else pc = code[pc + 10 + 1];break; 
        }
    }
}