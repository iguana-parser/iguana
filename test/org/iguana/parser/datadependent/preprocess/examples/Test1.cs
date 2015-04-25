class Hello
{
   static void Main() {
      System.Console.WriteLine(@"hello, 
#if Debug
      world
#else
      Nebraska
#endif
      ");
   }
}
