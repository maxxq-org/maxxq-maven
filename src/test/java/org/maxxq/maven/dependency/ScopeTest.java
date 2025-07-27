package org.maxxq.maven.dependency;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScopeTest {

    @Test
    public void transitiveScope() {
        Assertions.assertEquals( Scope.COMPILE, Scope.COMPILE.transitiveScope( Scope.COMPILE )  );
        Assertions.assertEquals( Scope.RUNTIME, Scope.COMPILE.transitiveScope( Scope.RUNTIME)  );
        Assertions.assertEquals( Scope.TEST, Scope.COMPILE.transitiveScope( Scope.TEST)  );
        Assertions.assertEquals( Scope.PROVIDED, Scope.COMPILE.transitiveScope( Scope.PROVIDED)  );
        Assertions.assertEquals( Scope.COMPILE, Scope.COMPILE.transitiveScope( Scope.IMPORT)  );
        
        Assertions.assertEquals( Scope.TEST, Scope.TEST.transitiveScope( Scope.TEST)  );
        Assertions.assertEquals( Scope.TEST, Scope.TEST.transitiveScope( Scope.RUNTIME)  );
        Assertions.assertEquals( Scope.TEST, Scope.TEST.transitiveScope( Scope.COMPILE)  );
        Assertions.assertEquals( Scope.TEST, Scope.TEST.transitiveScope( Scope.PROVIDED)  );
        Assertions.assertEquals( Scope.TEST, Scope.TEST.transitiveScope( Scope.IMPORT)  );
        
        Assertions.assertEquals( Scope.TEST, Scope.RUNTIME.transitiveScope( Scope.TEST)  );
        Assertions.assertEquals( Scope.RUNTIME, Scope.RUNTIME.transitiveScope( Scope.COMPILE)  );
        Assertions.assertEquals( Scope.RUNTIME, Scope.RUNTIME.transitiveScope( Scope.RUNTIME)  );
        Assertions.assertEquals( Scope.RUNTIME, Scope.RUNTIME.transitiveScope( Scope.PROVIDED)  );
        Assertions.assertEquals( Scope.RUNTIME, Scope.RUNTIME.transitiveScope( Scope.IMPORT)  );
        
        Assertions.assertEquals( Scope.TEST, Scope.PROVIDED.transitiveScope( Scope.TEST)  );
        Assertions.assertEquals( Scope.PROVIDED, Scope.PROVIDED.transitiveScope( Scope.COMPILE)  );
        Assertions.assertEquals( Scope.PROVIDED, Scope.PROVIDED.transitiveScope( Scope.RUNTIME)  );
        Assertions.assertEquals( Scope.PROVIDED, Scope.PROVIDED.transitiveScope( Scope.PROVIDED)  );
        Assertions.assertEquals( Scope.PROVIDED, Scope.PROVIDED.transitiveScope( Scope.IMPORT)  );
    }
    
}
