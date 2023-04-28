import Constants.Constants;

import java.util.Arrays;

public class Register {
//    int value;
    int numberOfBytes;
    Character[] hexData;
    Register(int numberOfBytes) {
        this.numberOfBytes = numberOfBytes;
        this.hexData = new Character[numberOfBytes];
        setValue(0);
    }

    Register(int numberOfBytes, int value)
    {
        this.numberOfBytes = numberOfBytes;
        this.hexData = new Character[numberOfBytes];
        setValue(value);
    }

    int value()
    {
        return Conversion.ConvertHexStringToInt(hexData);
    }

    void setValue(int v)
    {
        v %= (1L<<(8 * numberOfBytes));
        Character[] newHexData = Conversion.ConvertIntToHexCharacterArray(v);
        assert(newHexData.length <= hexData.length);
        Arrays.fill(hexData, '0');
        for(int i = 1; i <= newHexData.length; i++)
        {
            hexData[hexData.length - i] = newHexData[newHexData.length - i];
        }
    }

    public void add(Register r) {
        assert(r.numberOfBytes == this.numberOfBytes);
        this.setValue(this.value() + r.value());
    }

    public void subtract(Register r) {
        assert(r.numberOfBytes == this.numberOfBytes);
        this.setValue(this.value() - r.value());
    }
    public void multiply(Register r) {
        assert(r.numberOfBytes == this.numberOfBytes);
        this.setValue(this.value() * r.value());
    }

    public void divide(Register r) {
        assert(r.numberOfBytes == this.numberOfBytes);
        this.setValue(this.value() / r.value());

    }

    public Register cmp(Register r1, Register r2) {
        assert(r1.numberOfBytes == r2.numberOfBytes);
        return new Register(r1.numberOfBytes, r1.value() - r2.value());
    }

    private int maxValue() {
        return (1<<(8 * this.numberOfBytes));
    }

    public void setFlags(Register flags) {
        flags.setValue(0);
        if(value() == 0)
            flags.setValue(flags.value() + Constants.ZF);
        if(this.value() >= (maxValue() / 2))
        {
            flags.setValue(flags.value() + Constants.OF);
        }
        if(this.value() >= maxValue())
        {
            flags.setValue(flags.value() + Constants.CF);
            this.setValue(this.value() % maxValue());
        }
        if((this.value() & (maxValue() / 2)) > 0)
        {
            flags.setValue(flags.value() + Constants.SF);
        }
    }
}
