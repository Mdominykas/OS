import Constants.*;

import java.util.Arrays;

public class Register {
    //    int value;
    static Register PI;
    int numberOfBytes;
    Character[] hexData;

    Register(int numberOfBytes) {
        this.numberOfBytes = numberOfBytes;
        this.hexData = new Character[numberOfBytes];
        setValue(0);
    }

    Register(int numberOfBytes, int value) {
        this.numberOfBytes = numberOfBytes;
        this.hexData = new Character[numberOfBytes];
        setValue(value);
    }

    int value() {
        return Conversion.ConvertHexStringToInt(hexData);
    }

    void setValue(int v) {
        v %= range();
        Character[] newHexData = Conversion.ConvertIntToHexCharacterArray(v);
        assert (newHexData.length <= hexData.length);
        Arrays.fill(hexData, '0');
        for (int i = 1; i <= newHexData.length; i++) {
            hexData[hexData.length - i] = newHexData[newHexData.length - i];
        }
    }

    public void add(int otherValue, Register flags){
        assert(otherValue >= 0);
        int unfixedValue = value() + otherValue;
        setFlags(value(), otherValue, unfixedValue, flags);
        this.setValue(fixValue(unfixedValue));
    }

    public void add(Register r, Register flags) {
        assert (numberOfBytes == r.numberOfBytes);
        add(r.value(), flags);
    }

    public void subtract(int otherValue, Register flags){
        assert(otherValue >= 0);
        int unfixedValue = value() - otherValue;
        setFlags(value(), otherValue, unfixedValue, flags);
        this.setValue(fixValue(unfixedValue));
    }
    public void subtract(Register r, Register flags) {
        assert (numberOfBytes == r.numberOfBytes);
        subtract(r.value(), flags);
    }

    public void multiply(int otherValue, Register flags){
        assert(otherValue >= 0);
        int unfixedValue = value() * otherValue;
        setFlags(value(), otherValue, unfixedValue, flags);
        this.setValue(fixValue(unfixedValue));
    }
    public void multiply(Register r, Register flags) {
        assert (numberOfBytes == r.numberOfBytes);
        multiply(r.value(), flags);
    }

    public void divide(int otherValue, Register flags){
        assert(otherValue >= 0);
        if (otherValue == 0) {
            PI.setValue(PIValues.DivisionByZero);
        }
        int unfixedValue = value() / otherValue;
        setFlags(value(), otherValue, unfixedValue, flags);
        this.setValue(fixValue(unfixedValue));

    }
    public void divide(Register r, Register flags) {
        assert (numberOfBytes == r.numberOfBytes);
        divide(r.value(), flags);
    }

    public void cmp(int otherValue, Register flags){
        int unfixedValue = value() - otherValue;
        setFlags(value(), otherValue, unfixedValue, flags);
    }
    public void cmp(Register r, Register flags) {
        assert (numberOfBytes == r.numberOfBytes);
        cmp(r.value(), flags);
    }

    private int fixValue(int unfixedValue) {
        return ((unfixedValue % range()) + range()) % range();
    }

    public int range() {
        return (1 << (4 * this.numberOfBytes));
    }

    private int sign(int value){
        return (this.value() & (range() / 2)) > 0 ? 1 : 0;
    }

    private void clearFlags(Register flags) {
        flags.setValue(0);
    }

    private void setSignAndZeroFlags(int value, Register flags) {
        if (value == 0) {
            flags.setValue(flags.value() + Constants.ZF);
        }
        if (sign(value) > 0) {
            flags.setValue(flags.value() + Constants.SF);
        }
    }

    private void setCarryFlag(int unfixedValue, Register flags){
        if((unfixedValue < 0) || (unfixedValue >= range())){
            flags.setValue(flags.value() + Constants.CF);
        }
    }

    private void setOverflowFlag(int operand1, int operand2, int fixedValue, Register flags){
        if((sign(operand1) == sign(operand2)) && (sign(operand1) != sign(fixedValue)))
            flags.setValue(flags.value() + Constants.OF);
    }

    private void setFlags(int operand1, int operand2, int unfixedValue, Register flags) {
        clearFlags(flags);
        setCarryFlag(unfixedValue, flags);
        int fixedValue = fixValue(unfixedValue);
        setSignAndZeroFlags(fixedValue, flags);
        setOverflowFlag(operand1, operand2, fixedValue, flags);
    }

    public String toString() {
        return Conversion.characterArrayToString(hexData);
    }
}
