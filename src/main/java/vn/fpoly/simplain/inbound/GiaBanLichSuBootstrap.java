package vn.fpoly.simplain.inbound;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GiaBanLichSuBootstrap implements ApplicationRunner {
    private final LichSuBaoGiaService service;

    public GiaBanLichSuBootstrap(LichSuBaoGiaService service) {
        this.service = service;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        service.ghiMocBanDauChoSkuHienCo();
    }
}
