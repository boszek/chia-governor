package pl.thatisit.plotter.drivespace;

import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.*;

public class DrivesTest {
    final String mounts = "/dev/loop4 /snap/snap-store/518 squashfs ro,nodev,relatime 0 0\n" +
            "/dev/sda /media/boszek/Chia\\040Toshiba\\0404TB ext4 rw,nosuid,nodev,relatime 0 0" +
            "/dev/loop5 /snap/gtk-common-themes/1515 squashfs ro,nodev,relatime 0 0\n" +
            "/dev/loop6 /snap/core18/1988 squashfs ro,nodev,relatime 0 0\n" +
            "/dev/loop7 /snap/snapd/11036 squashfs ro,nodev,relatime 0 0\n" +
            "/dev/sdb1 /boot/efi vfat rw,relatime,fmask=0077,dmask=0077,codepage=437,iocharset=iso8859-1,shortname=mixed,errors=remount-ro 0 0\n" +
            "tmpfs /run/user/125 tmpfs rw,nosuid,nodev,relatime,size=1205748k,mode=700,uid=125,gid=130 0 0\n" +
            "gvfsd-fuse /run/user/125/gvfs fuse.gvfsd-fuse rw,nosuid,nodev,relatime,user_id=125,group_id=130 0 0\n" +
            "tmpfs /run/user/1000 tmpfs rw,nosuid,nodev,relatime,size=1205748k,mode=700,uid=1000,gid=1000 0 0\n" +
            "gvfsd-fuse /run/user/1000/gvfs fuse.gvfsd-fuse rw,nosuid,nodev,relatime,user_id=1000,group_id=1000 0 0\n" +
            "/dev/loop8 /snap/core/10958 squashfs ro,nodev,relatime 0 0\n" +
            "/dev/loop9 /snap/intellij-idea-community/299 squashfs ro,nodev,relatime 0 0\n";


    @Test
    public void shouldParseMountsOutput() {
        var result = Drives.mountRoots(List.of(mounts.split("\n")));

        assertThat(result.get(1)).isEqualTo("/media/boszek/Chia Toshiba 4TB");
    }
}