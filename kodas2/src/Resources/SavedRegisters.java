package Resources;

import Utils.RegisterContainer;

public class SavedRegisters {
    int R1, R2, R3, FLAGS, IC, CS, DS, SI, PI;
    public void save(RegisterContainer registerContainer)
    {
        this.R1 = registerContainer.R1.value();
        this.R2 = registerContainer.R2.value();
        this.R3 = registerContainer.R3.value();
        this.FLAGS = registerContainer.FLAGS.value();
        this.IC = registerContainer.IC.value();
        this.CS = registerContainer.CS.value();
        this.DS = registerContainer.DS.value();
        this.SI = registerContainer.SI.value();
        this.PI = registerContainer.PI.value();
    }

    public void load(RegisterContainer registerContainer)
    {
        registerContainer.R1.setValue(R1);
        registerContainer.R2.setValue(R2);
        registerContainer.R3.setValue(R3);
        registerContainer.FLAGS.setValue(FLAGS);
        registerContainer.IC.setValue(IC);
        registerContainer.CS.setValue(CS);
        registerContainer.DS.setValue(DS);
        registerContainer.SI.setValue(SI);
        registerContainer.PI.setValue(PI);

    }
}
