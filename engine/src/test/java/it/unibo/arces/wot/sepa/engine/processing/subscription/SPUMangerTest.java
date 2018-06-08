package it.unibo.arces.wot.sepa.engine.processing.subscription;

import it.unibo.arces.wot.sepa.commons.response.Response;
import it.unibo.arces.wot.sepa.commons.response.UpdateResponse;
import it.unibo.arces.wot.sepa.commons.sparql.BindingsResults;
import it.unibo.arces.wot.sepa.engine.processing.subscriptions.ISubscriptionProcUnit;
import it.unibo.arces.wot.sepa.engine.processing.subscriptions.SPUManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SPUMangerTest {

    private SPUManager spuManger;

    @Before
    public void Init(){
        spuManger = new SPUManager();
    }

    @Test
    public void registerTest(){
        FakeSPU spu = new FakeSPU("Test");
        spuManger.register(spu);
        Assert.assertEquals("SPU not registered",spuManger.size(),1);
    }

    @Test
    public void unRegisterTest(){
        FakeSPU spu = new FakeSPU("123Test");
        spuManger.register(spu);
        Assert.assertEquals("SPU not registered",spuManger.size(),1);
        spuManger.unRegister("123Test");
        Assert.assertEquals("SPU not succesfully deleted",spuManger.size(),0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unRegisterInvalidID(){
        spuManger.unRegister("pluto");
    }

    @Test
    public void isValidUIDTest(){
        Assert.assertFalse(spuManger.isValidSpuId("Cap"));
        spuManger.register(new FakeSPU("Ironman"));
        Assert.assertTrue(spuManger.isValidSpuId("Ironman"));
    }

    private class FakeSPU implements ISubscriptionProcUnit{

        private String uid;

        public FakeSPU(String uid){

            this.uid = uid;
        }
        @Override
        public Response init() {
            return null;
        }

        @Override
        public BindingsResults getCurrentResults() {
            return null;
        }

        @Override
        public void terminate() {

        }

        @Override
        public String getUUID() {
            return uid;
        }

        @Override
        public void process(UpdateResponse res) {

        }

        @Override
        public void run() {

        }
    }
}

