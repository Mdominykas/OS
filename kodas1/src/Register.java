public class Register {
    int value;
    int numberOfBytes;
    Register(int numberOfBytes) {
        this.numberOfBytes = numberOfBytes;
        this.value = 0;
    }

    Register(int numberOfBytes, int value) {
        this.numberOfBytes = numberOfBytes;
        this.value = value;
    }

    public void add(Register r) {
        assert(r.numberOfBytes == this.numberOfBytes);
        this.value += r.value;
    }

    public void subtract(Register r) {
        assert(r.numberOfBytes == this.numberOfBytes);
        this.value -= r.value;
    }
    public void multiply(Register r) {
        assert(r.numberOfBytes == this.numberOfBytes);
        this.value *= r.value;
    }

    public void divide(Register r) {
        assert(r.numberOfBytes == this.numberOfBytes);
        this.value /= r.value;
    }

    public Register cmp(Register r1, Register r2) {
        assert(r1.numberOfBytes == r2.numberOfBytes);
        return new Register(r1.numberOfBytes, r1.value - r2.value);
    }

    private int maxValue() {
        return (1<<(8 * this.numberOfBytes));
    }

    public void setFlags(Register flags) {
        flags.value = 0;
        if(value == 0)
            flags.value += (Constants.ZF);
        if(this.value >= (maxValue() / 2))
        {
            flags.value += Constants.OF;
        }
        if(this.value >= maxValue())
        {
            flags.value += (Constants.CF);
            this.value %= maxValue();
        }
        if((this.value & (maxValue() / 2)) > 0)
        {
            flags.value += Constants.SF;
        }
    }
}
