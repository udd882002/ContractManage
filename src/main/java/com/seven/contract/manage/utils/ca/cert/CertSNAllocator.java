package com.seven.contract.manage.utils.ca.cert;

import java.math.BigInteger;

public interface CertSNAllocator {
    BigInteger incrementAndGet() throws Exception;
}
